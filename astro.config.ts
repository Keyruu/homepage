// @ts-check
import mdx from "@astrojs/mdx";
import sitemap from "@astrojs/sitemap";
import remarkCallout from "@r4ai/remark-callout";
import { defineConfig } from "astro/config";
import rehypeAutolinkHeadings from "rehype-autolink-headings";
import rehypeSlug from "rehype-slug";
import rehypeExternalLinks from "rehype-external-links";
import remarkSectionize from "remark-sectionize";
import remarkWikilink from "remark-wiki-link";
import { remarkReadingTime } from "./src/plugins/reading-time.mjs";

// https://astro.build/config
export default defineConfig({
  site: "https://example.com",
  integrations: [mdx(), sitemap()],

  markdown: {
    shikiConfig: {
      theme: "ayu-dark",
    },
    remarkPlugins: [
      // [remarkToc, { heading: 'toc', maxDepth: 3 }],
      remarkCallout,
      [
        remarkWikilink,
        {
          aliasDivider: "|",
          hrefTemplate: (permalink: String) => `/blog/${permalink}`,
        },
      ],
      remarkSectionize,
      remarkReadingTime,
    ],
    rehypePlugins: [
      rehypeSlug,
      [rehypeAutolinkHeadings, { behavior: "prepend" }],
      [
        rehypeExternalLinks,
        {
          target: "_blank",
          rel: ["noopener"],
          content: {
            type: "raw",
            value:
              '<svg class="external-icon" viewBox="0 0 512 512"><path d="M320 0H288V64h32 82.7L201.4 265.4 178.7 288 224 333.3l22.6-22.6L448 109.3V192v32h64V192 32 0H480 320zM32 32H0V64 480v32H32 456h32V480 352 320H424v32 96H64V96h96 32V32H160 32z"></path></svg>',
          },
        },
      ],
    ],
  },

  vite: {},
});
