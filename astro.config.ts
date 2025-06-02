// @ts-check
import mdx from '@astrojs/mdx';
import sitemap from '@astrojs/sitemap';
import remarkCallout from "@r4ai/remark-callout";
import { defineConfig } from 'astro/config';
import rehypeAutolinkHeadings from 'rehype-autolink-headings';
import remarkSectionize from "remark-sectionize";
import remarkWikilink from "remark-wiki-link";
import { remarkReadingTime } from './src/plugins/reading-time.mjs';

import tailwindcss from '@tailwindcss/vite';

// https://astro.build/config
export default defineConfig({
  site: 'https://example.com',
  integrations: [mdx(), sitemap()],

  markdown: {
    shikiConfig: {
      theme: 'ayu-dark'
    },
    remarkPlugins: [
      // [remarkToc, { heading: 'toc', maxDepth: 3 }],
      remarkCallout,
      [remarkWikilink, {
        aliasDivider: '|',
        hrefTemplate: (permalink: String) => `/blog/${permalink}`,
      }],
      remarkSectionize,
      remarkReadingTime,
    ],
    rehypePlugins: [
      [rehypeAutolinkHeadings, { behavior: 'append' }]
    ]
  },

  vite: {
    plugins: [tailwindcss()],
  },
});
