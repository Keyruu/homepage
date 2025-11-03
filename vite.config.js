import { defineConfig } from 'vite'
import { resolve } from 'path'

export default defineConfig({
  // Build in library mode for SSR apps
  build: {
    outDir: 'src/main/resources/public/dist',
    emptyOutDir: true,

    // CSS code splitting - extract CSS into separate files
    cssCodeSplit: true,

    // Multiple entry points
    rollupOptions: {
      input: {
        main: resolve(__dirname, 'src/main/resources/js/main.js'),
        blog: resolve(__dirname, 'src/main/resources/js/blog.js'),
      },
      output: {
        // Use simple names (not hashed) for easier SSR integration
        entryFileNames: '[name].js',
        chunkFileNames: '[name].js',
        assetFileNames: '[name].[ext]'
      }
    },

    // Minification
    minify: 'esbuild', // Fast and good enough for most cases
    sourcemap: process.env.NODE_ENV === 'development',

    // Target modern browsers
    target: 'es2020'
  }
})
