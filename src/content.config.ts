import { glob } from "astro/loaders";
import { defineCollection, z } from "astro:content";

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
  loader: glob({ base: "./src/content/projects", pattern: "**/*.md" }),
  schema: z.object({
    name: z.string(),
    description: z.string(),
    url: z.string().url(),
    stars: z.number().optional(),
    language: z.string().optional(),
    topics: z.array(z.string()).optional(),
    archived: z.boolean().optional(),
    order: z.number(),
  }),
});

export const collections = { blog, tech, music, projects };
