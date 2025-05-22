// { depth: number; text: string; id: string }[]
interface Heading {
	depth: number;
	text: string;
	id: string;
}

// {
//   subheadings: never[];
//   depth: number;
//   text: string;
//   id: string;
// }[]
interface Toc {
	subheadings: Toc[];
	depth: number;
	text: string;
	id: string;
}