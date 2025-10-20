#!/bin/bash

# ============= VERSION CONFIGURATION =============
# Update these versions as needed
PRISM_VERSION="1.30.0"
ALPINE_VERSION="3.14.3"
ALPINE_AJAX_VERSION="0.12.4"
CSS_SCOPE_INLINE_VERSION="main" # Using main branch

# PrismJS language components to download
# See available languages at: https://prismjs.com/#supported-languages
PRISM_LANGUAGES=(
  "bash"
  "shell"
  "javascript"
  "typescript"
  "scala"
  "java"
  "python"
  "rust"
  "go"
  "yaml"
  "json"
  "toml"
  "sql"
  "nix"
  "docker"
  "html"
  "css"
  "markdown"
  "xml"
  "graphql"
  "systemd"
)
# ================================================

# Remove old vendor
rm -rf src/main/resources/public/js/vendor
rm -rf src/main/resources/public/css/vendor

# Create directories
mkdir -p src/main/resources/public/js/vendor
mkdir -p src/main/resources/public/css/vendor

# PrismJS Core and Plugins
echo "Downloading PrismJS v${PRISM_VERSION}..."
curl -L "https://cdnjs.cloudflare.com/ajax/libs/prism/${PRISM_VERSION}/prism.min.js" \
  -o src/main/resources/public/js/vendor/prism.min.js

curl -L "https://cdnjs.cloudflare.com/ajax/libs/prism/${PRISM_VERSION}/plugins/autoloader/prism-autoloader.min.js" \
  -o src/main/resources/public/js/vendor/prism-autoloader.min.js

curl -L "https://cdnjs.cloudflare.com/ajax/libs/prism/${PRISM_VERSION}/plugins/line-numbers/prism-line-numbers.min.js" \
  -o src/main/resources/public/js/vendor/prism-line-numbers.min.js

curl -L "https://cdnjs.cloudflare.com/ajax/libs/prism/${PRISM_VERSION}/plugins/toolbar/prism-toolbar.min.js" \
  -o src/main/resources/public/js/vendor/prism-toolbar.min.js

curl -L "https://cdnjs.cloudflare.com/ajax/libs/prism/${PRISM_VERSION}/plugins/copy-to-clipboard/prism-copy-to-clipboard.min.js" \
  -o src/main/resources/public/js/vendor/prism-copy-to-clipboard.min.js

# Download PrismJS language components
echo "Downloading PrismJS language components..."
mkdir -p src/main/resources/public/js/vendor/components
for lang in "${PRISM_LANGUAGES[@]}"; do
  echo "  - Downloading $lang..."
  curl -sL "https://cdnjs.cloudflare.com/ajax/libs/prism/${PRISM_VERSION}/components/prism-${lang}.min.js" \
    -o "src/main/resources/public/js/vendor/components/prism-${lang}.min.js"
done

# PrismJS CSS
echo "Downloading PrismJS CSS v${PRISM_VERSION}..."
curl -L "https://cdnjs.cloudflare.com/ajax/libs/prism/${PRISM_VERSION}/plugins/line-numbers/prism-line-numbers.min.css" \
  -o src/main/resources/public/css/vendor/prism-line-numbers.min.css

curl -L "https://cdnjs.cloudflare.com/ajax/libs/prism/${PRISM_VERSION}/plugins/toolbar/prism-toolbar.min.css" \
  -o src/main/resources/public/css/vendor/prism-toolbar.min.css

# CSS Scope Inline
echo "Downloading CSS Scope Inline (${CSS_SCOPE_INLINE_VERSION})..."
curl -L "https://cdn.jsdelivr.net/gh/gnat/css-scope-inline@${CSS_SCOPE_INLINE_VERSION}/script.js" \
  -o src/main/resources/public/js/vendor/css-scope-inline.min.js

# Alpine.js
echo "Downloading Alpine.js v${ALPINE_VERSION}..."
curl -L "https://cdn.jsdelivr.net/npm/alpinejs@${ALPINE_VERSION}/dist/cdn.min.js" \
  -o src/main/resources/public/js/vendor/alpine.min.js

# Alpine AJAX
echo "Downloading Alpine AJAX v${ALPINE_AJAX_VERSION}..."
curl -L "https://cdn.jsdelivr.net/npm/@imacrayon/alpine-ajax@${ALPINE_AJAX_VERSION}/dist/cdn.min.js" \
  -o src/main/resources/public/js/vendor/alpine-ajax.min.js

echo ""
echo "✅ All dependencies downloaded!"
echo ""
echo "Versions:"
echo "  - PrismJS: ${PRISM_VERSION} (with ${#PRISM_LANGUAGES[@]} languages)"
echo "  - Alpine.js: ${ALPINE_VERSION}"
echo "  - Alpine AJAX: ${ALPINE_AJAX_VERSION}"
echo "  - CSS Scope Inline: ${CSS_SCOPE_INLINE_VERSION}"
echo ""
echo "Languages downloaded: ${PRISM_LANGUAGES[*]}"
