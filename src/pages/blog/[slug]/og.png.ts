import type { APIRoute, GetStaticPaths } from "astro";
import { getCollection } from "astro:content";
import { generateOgImageBuffer } from "../../../utils/generate-og-image";

export const getStaticPaths: GetStaticPaths = async () => {
  const posts = await getCollection("blog", ({ data }) => !data.draft);
  return posts.map((post) => ({
    params: { slug: post.id },
    props: { post },
  }));
};

export const GET: APIRoute = async ({ props }) => {
  const { post } = props;
  const image = await generateOgImageBuffer({
    title: post.data.title,
    description: post.data.description,
  });

  return new Response(new Uint8Array(image), {
    headers: { "Content-Type": "image/png" },
  });
};

export const prerender = true;
