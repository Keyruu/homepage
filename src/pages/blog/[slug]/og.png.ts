import { getCollection } from "astro:content";
import { generateOgImageBuffer } from "../../../utils/generateOgImage";

export async function getStaticPaths() {
  const posts = await getCollection("blog", ({ data }) => !data.draft);
  return posts.map((post) => ({
    params: { slug: post.id },
    props: { post },
  }));
}

export async function GET({ props }) {
  const { post } = props;
  const image = await generateOgImageBuffer({
    title: post.data.title,
    description: post.data.description,
  });

  return new Response(image, {
    headers: { "Content-Type": "image/png" },
  });
}

export const prerender = true;
