import type { CollectionEntry } from "astro:content";

export function isPublished(
  post: CollectionEntry<"blog">,
): post is CollectionEntry<"blog"> & { data: { pubDate: Date } } {
  return post.data.pubDate != null && !post.data.draft;
}
