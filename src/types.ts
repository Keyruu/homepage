export interface Heading {
  depth: number;
  slug: string;
  text: string;
}

export interface Toc {
  subheadings: Toc[];
  depth: number;
  slug: string;
  text: string;
}
