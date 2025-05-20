// @ts-check
import { defineConfig } from 'astro/config';
import mdx from '@astrojs/mdx';
import sitemap from '@astrojs/sitemap';
import remarkToc from 'remark-toc';
import remarkCallout from "@r4ai/remark-callout";
import remarkWikilink from "remark-wiki-link";

import tailwindcss from '@tailwindcss/vite';

// https://astro.build/config
export default defineConfig({
  site: 'https://example.com',
  integrations: [mdx(), sitemap()],

  markdown: {
    remarkPlugins: [
      [remarkToc, { heading: 'toc', maxDepth: 3 }],
      remarkCallout,
      [remarkWikilink, { aliasDivider: '|', hrefTemplate: (permalink: String) => `/blog/${permalink}` }],
    ],
  },

  vite: {
    plugins: [tailwindcss()],
  },
});
