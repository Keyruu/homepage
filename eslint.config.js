import eslint from "@eslint/js";
import { defineConfig } from "eslint/config";
import functional from "eslint-plugin-functional";
import astro from "eslint-plugin-astro";
import tseslint from "typescript-eslint";
import prettier from "eslint-config-prettier";

export default defineConfig(
  // Base configs
  eslint.configs.recommended,
  tseslint.configs.strictTypeChecked,
  tseslint.configs.stylisticTypeChecked,

  // Astro
  ...astro.configs.recommended,

  // Functional — lite preset (practical for Astro)
  functional.configs.externalTypeScriptRecommended,
  functional.configs.lite,
  functional.configs.stylistic,

  // Prettier must be last to disable conflicting rules
  prettier,

  // Global settings
  {
    languageOptions: {
      parserOptions: {
        projectService: true,
      },
    },
  },

  // TypeScript files — strict rules
  {
    files: ["**/*.ts", "**/*.tsx"],
    rules: {
      // Strict TypeScript best practices
      "@typescript-eslint/no-explicit-any": "error",
      "@typescript-eslint/no-non-null-assertion": "error",
      "@typescript-eslint/prefer-nullish-coalescing": "error",
      "@typescript-eslint/prefer-optional-chain": "error",
      "@typescript-eslint/strict-boolean-expressions": "warn",
      "@typescript-eslint/consistent-type-imports": ["error", { prefer: "type-imports" }],
      "@typescript-eslint/consistent-type-definitions": ["error", "interface"],
    },
  },

  // Astro files — relax rules that don't work well in .astro
  {
    files: ["**/*.astro"],
    rules: {
      // Astro frontmatter is inherently imperative
      "functional/no-expression-statements": "off",
      "functional/no-conditional-statements": "off",
      "functional/no-return-void": "off",
      "functional/functional-parameters": "off",

      // Astro components are not modules in the traditional sense
      "@typescript-eslint/consistent-type-imports": "off",

      // These often false-positive in Astro templates
      "@typescript-eslint/no-unsafe-assignment": "off",
      "@typescript-eslint/no-unsafe-member-access": "off",
      "@typescript-eslint/no-unsafe-call": "off",
      "@typescript-eslint/no-unsafe-return": "off",
      "@typescript-eslint/no-unsafe-argument": "off",
    },
  },

  // Client-side scripts in Astro (virtual .ts files)
  {
    files: ["**/*.astro/*.ts", "**/*.astro/*.js"],
    rules: {
      // Scripts use DOM APIs which are inherently imperative
      "functional/no-expression-statements": "off",
      "functional/no-conditional-statements": "off",
      "functional/no-return-void": "off",
      "functional/immutable-data": "off",
      "functional/no-let": "off",
      "functional/functional-parameters": "off",
      "functional/prefer-immutable-types": "off",

      // Type-aware rules don't work in virtual script files
      "@typescript-eslint/no-unsafe-assignment": "off",
      "@typescript-eslint/no-unsafe-member-access": "off",
      "@typescript-eslint/no-unsafe-call": "off",
      "@typescript-eslint/no-unsafe-return": "off",
      "@typescript-eslint/no-unsafe-argument": "off",
    },
  },

  // JS config files — disable type-checked rules
  {
    files: ["**/*.js", "**/*.mjs"],
    extends: [tseslint.configs.disableTypeChecked, functional.configs.disableTypeChecked],
  },

  // Ignore build output, dependencies, and legacy bundled files
  {
    ignores: ["dist/", ".astro/", "node_modules/", "src/main/"],
  },
);
