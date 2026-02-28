{
  pkgs,
}:
pkgs.writeShellApplication {
  name = "update-projects";
  runtimeInputs = with pkgs; [
    curl
    jq
    coreutils
    gnugrep
    gnused
  ];
  text = ''
    PROJECTS_DIR="$PWD/src/content/projects"

    for file in "$PROJECTS_DIR"/*.md; do
      [ -f "$file" ] || continue

      echo "Processing $file..."

      repo=$(grep -E '^url:' "$file" | sed 's|url: https://github.com/||' | sed 's/"/\\"/g' | xargs)

      if [ -z "$repo" ]; then
        echo "  Skipping - no url found"
        continue
      fi

      projname=$(basename "$file" .md)

      order=$(grep -E '^order:' "$file" | sed 's/order: //' | tr -d ' ')

      echo "  Fetching $repo..."

      headers=(
        -H "Accept: application/vnd.github.v3+json"
      )

      data=$(curl -s "''${headers[@]}" "https://api.github.com/repos/$repo")

      description=$(echo "$data" | jq -r '.description // ""')
      language=$(echo "$data" | jq -r '.language // ""')
      stars=$(echo "$data" | jq -r '.stargazers_count // 0')
      archived=$(echo "$data" | jq -r '.archived')

      topics=$(echo "$data" | jq -r '[.topics[]?]')

      if [ -z "$order" ]; then
        order=0
      fi

      cat > "$file" <<EOF
    ---
    name: $projname
    description: "$description"
    url: https://github.com/$repo
    stars: $stars
    language: "$language"
    topics: $topics
    archived: $archived
    forked: $forked
    order: $order
    ---
    EOF

      echo "  Updated $file"
    done

    echo "Done!"
  '';
}
