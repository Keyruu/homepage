// yoinked from https://mfyz.com/generate-beautiful-og-images-astro-satori/
import { Resvg } from "@resvg/resvg-js";
import satori from "satori";
import fs from "fs";
import path from "path";

export async function generateOgImageBuffer(post: { title: string; description: string }) {
  const svg = await generateOgImage(post);
  const resvg = new Resvg(svg);
  const pngData = resvg.render();
  return pngData.asPng();
}

async function loadFont(name: string) {
  const extensions = [".ttf", ".woff", ".woff2"];
  for (const ext of extensions) {
    const fontPath = path.resolve(`./public/fonts/${name}${ext}`);
    if (fs.existsSync(fontPath)) {
      return fs.readFileSync(fontPath);
    }
  }
  throw new Error(`Font not found: ${name}`);
}

function truncateText(text: string, maxLength: number) {
  if (text.length <= maxLength) return text;
  return text.slice(0, maxLength - 3) + "...";
}

async function generateOgImage(post: { title: string; description: string }) {
  const [zodiakFont, satoshiRegular, faviconPng] = await Promise.all([
    loadFont("Zodiak-Extrabold"),
    loadFont("Satoshi-Regular"),
    fs.readFileSync("./public/favicon-32x32.png"),
  ]);

  const svg = await satori(
    {
      type: "div",
      props: {
        style: {
          width: "100%",
          height: "100%",
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
          background: `linear-gradient(135deg, #0a0c14 0%, #0f1529 50%, #0a0c14 100%)`,
          position: "relative",
          overflow: "hidden",
        },
        children: [
          {
            type: "div",
            props: {
              style: {
                position: "absolute",
                top: 0,
                left: 0,
                right: 0,
                bottom: 0,
                background:
                  "radial-gradient(ellipse at 20% 0%, rgba(43, 101, 255, 0.15) 0%, transparent 50%)",
              },
            },
          },
          {
            type: "div",
            props: {
              style: {
                position: "absolute",
                top: 0,
                left: 0,
                right: 0,
                bottom: 0,
                background:
                  "radial-gradient(ellipse at 80% 20%, rgba(4, 54, 84, 0.4) 0%, transparent 50%)",
              },
            },
          },
          {
            type: "div",
            props: {
              style: {
                position: "absolute",
                top: 0,
                left: 0,
                right: 0,
                bottom: 0,
                background:
                  "radial-gradient(ellipse at 50% 100%, rgba(10, 12, 20, 0.5) 0%, transparent 70%)",
              },
            },
          },
          {
            type: "div",
            props: {
              style: {
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                padding: "60px",
                textAlign: "center",
                maxWidth: "900px",
              },
              children: [
                {
                  type: "h1",
                  props: {
                    style: {
                      fontSize: "72px",
                      fontWeight: 800,
                      color: "#c8d7eb",
                      fontFamily: "Zodiak",
                      lineHeight: 1.1,
                      marginBottom: "24px",
                      textShadow: "0 4px 20px rgba(0, 0, 0, 0.5)",
                    },
                    children: truncateText(post.title, 80),
                  },
                },
                {
                  type: "p",
                  props: {
                    style: {
                      fontSize: "28px",
                      color: "rgba(200, 215, 235, 0.7)",
                      fontFamily: "Satoshi",
                      lineHeight: 1.4,
                      maxWidth: "800px",
                    },
                    children: truncateText(post.description, 120),
                  },
                },
              ],
            },
          },
          {
            type: "div",
            props: {
              style: {
                position: "absolute",
                bottom: "40px",
                right: "50px",
                display: "flex",
                alignItems: "center",
                gap: "12px",
              },
              children: [
                {
                  type: "img",
                  props: {
                    src: `data:image/png;base64,${faviconPng.toString("base64")}`,
                    style: {
                      width: "32px",
                      height: "32px",
                    },
                  },
                },
                {
                  type: "span",
                  props: {
                    style: {
                      fontSize: "18px",
                      color: "rgba(200, 215, 235, 0.5)",
                      fontFamily: "Satoshi",
                    },
                    children: "keyruu.de",
                  },
                },
              ],
            },
          },
          {
            type: "div",
            props: {
              style: {
                position: "absolute",
                top: "50px",
                left: "50px",
                width: "8px",
                height: "8px",
                borderRadius: "50%",
                background: "#2b65ff",
                boxShadow: "0 0 20px #2b65ff, 0 0 40px #2b65ff",
              },
            },
          },
        ],
      },
    },
    {
      width: 1200,
      height: 630,
      embedFont: true,
      fonts: [
        {
          name: "Zodiak",
          data: zodiakFont,
          style: "normal",
          weight: 800,
        },
        {
          name: "Satoshi",
          data: satoshiRegular,
          style: "normal",
          weight: 400,
        },
      ],
    },
  );

  return svg;
}
