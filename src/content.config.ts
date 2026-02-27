import { glob } from "astro/loaders";
import { defineCollection, z } from "astro:content";
import type { Loader } from "astro/loaders";

const githubReposLoader: Loader = {
  name: "github-repos",
  load: async ({ store }) => {
    const repos = [
      { owner: "Keyruu", repo: "shinyflakes" },
      { owner: "Keyruu", repo: "homepage" },
      { owner: "Keyruu", repo: "tabula" },
    ];
    for (const { owner, repo } of repos) {
      const response = await fetch(`https://api.github.com/repos/${owner}/${repo}`, {
        headers: {
          Accept: "application/vnd.github.v3+json",
          ...(process.env.GITHUB_TOKEN && { Authorization: `token ${process.env.GITHUB_TOKEN}` }),
        },
      });

      if (!response.ok) continue;

      const data = await response.json();

      const entry = {
        name: data.name,
        description: data.description || "",
        url: data.html_url,
        stars: data.stargazers_count,
        language: data.language,
        topics: data.topics || [],
        archived: data.archived,
        forked: data.forked,
      };

      store.set({ id: `${owner}/${repo}`, data: entry });
    }
  },
};

const blog = defineCollection({
  loader: glob({ base: "./src/content/blog", pattern: "**/*.{md,mdx}" }),
  schema: z.object({
    title: z.string(),
    description: z.string(),
    draft: z.boolean().default(false),
    pubDate: z.coerce.date().optional(),
    updatedDate: z.coerce.date().optional(),
    tags: z.array(z.string()),
  }),
});

const tech = defineCollection({
  loader: glob({ base: "./src/content/tech", pattern: "**/*.md" }),
  schema: z.object({
    name: z.string(),
    category: z.enum([
      "language",
      "framework",
      "editor",
      "desktop",
      "monitoring",
      "infra",
      "cloud",
      "tool",
      "gear",
    ]),
    url: z.string().url().optional(),
    icon: z.string().optional(),
  }),
});

const music = defineCollection({
  loader: glob({ base: "./src/content/music", pattern: "**/*.md" }),
  schema: z.object({
    title: z.string(),
    artists: z.string(),
    releaseDate: z.coerce.date(),
    releaseType: z.enum(["single", "ep", "album"]).default("single"),
    cover: z.string(),
    accentColor: z.string(),
    tracks: z.array(z.string()).optional(),
    spotify: z.string().url().optional(),
    appleMusic: z.string().url().optional(),
    youtube: z.string().url().optional(),
  }),
});

const projects = defineCollection({
  loader: githubReposLoader,
  schema: z.object({
    name: z.string(),
    description: z.string(),
    url: z.string().url(),
    stars: z.number().optional(),
    language: z.string().optional(),
    topics: z.array(z.string()).optional(),
    archived: z.boolean().optional(),
    forked: z.boolean().optional(),
  }),
});

export const collections = { blog, tech, music, projects };
