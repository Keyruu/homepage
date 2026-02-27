/* eslint-disable functional/no-return-void -- DOM setters return void */
/* eslint-disable functional/immutable-data -- DOM API requires mutation */
/* eslint-disable functional/prefer-immutable-types -- DOM APIs require mutable params */
/* eslint-disable @typescript-eslint/strict-boolean-expressions -- DOM nullable checks */
/* eslint-disable @typescript-eslint/no-non-null-assertion -- checked at function start */

const POLL_INTERVAL = 10_000;

interface Track {
  id: number;
  title: string;
  artists: { name: string }[] | null;
  image: string | null;
}

interface NowPlayingResponse {
  currently_playing: boolean;
  track: Track;
}

interface Listen {
  time: string;
  track: Track;
}

interface ListensResponse {
  items: Listen[];
}

type TrackData = NowPlayingResponse["track"];
type PlayingStatus = "playing" | "played" | "recent";

const statusText: Record<PlayingStatus, string> = {
  playing: "listening now",
  played: "last played",
  recent: "recently played",
};

interface UpdateParams {
  track: TrackData;
  status: PlayingStatus;
}

export function initNowPlaying(container: HTMLDivElement, apiUrl: string): void {
  const nowPlayingUrl = `${apiUrl}/now-playing`;
  const listensUrl = `${apiUrl}/listens?limit=1&period=all_time`;

  const coverImg = container.querySelector<HTMLImageElement>(".now-playing-cover");
  const skeletonCover = container.querySelector<HTMLDivElement>(".skeleton-cover");
  const trackEl = container.querySelector<HTMLParagraphElement>(".now-playing-track");
  const artistEl = container.querySelector<HTMLParagraphElement>(".now-playing-artist");
  const statusEl = container.querySelector<HTMLSpanElement>(".now-playing-status");

  if (!coverImg || !skeletonCover || !trackEl || !artistEl || !statusEl) return;

  function update({ track, status }: UpdateParams): void {
    if (!track.title) {
      container.style.display = "none";
      return;
    }

    const artistNames = track.artists?.map((a) => a.name).join(", ") ?? "Unknown Artist";
    const imageSrc = track.image;

    trackEl!.textContent = track.title;
    artistEl!.textContent = artistNames;
    statusEl!.textContent = statusText[status];

    if (imageSrc) {
      coverImg!.src = `https://fm.keyruu.de/images/full/${imageSrc}`;
      coverImg!.alt = `${track.title} cover`;
      coverImg!.style.display = "";
      skeletonCover!.style.display = "none";
    } else {
      coverImg!.style.display = "none";
      skeletonCover!.style.display = "none";
    }
  }

  async function fetchNowPlaying(): Promise<void> {
    try {
      const response = await fetch(nowPlayingUrl);
      if (!response.ok) return;
      const data = (await response.json()) as NowPlayingResponse;

      if (data.track.title) {
        update({ track: data.track, status: data.currently_playing ? "playing" : "played" });

        if (data.currently_playing) {
          container.classList.remove("paused");
        } else {
          container.classList.add("paused");
        }

        container.style.display = "";
      } else {
        await fetchRecentListen();
      }
    } catch {
      // silently fail and don't break the page
    }
  }

  async function fetchRecentListen(): Promise<void> {
    try {
      const response = await fetch(listensUrl);
      if (!response.ok) return;
      const data = (await response.json()) as ListensResponse;

      if (data.items.length > 0) {
        update({ track: data.items[0].track, status: "recent" });
        container.classList.add("paused");
        container.style.display = "";
      } else {
        container.style.display = "none";
      }
    } catch {
      container.style.display = "none";
    }
  }

  void fetchNowPlaying();
  setInterval(() => void fetchNowPlaying(), POLL_INTERVAL);
}
