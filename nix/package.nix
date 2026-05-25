{ pkgs, ... }:
pkgs.stdenvNoCC.mkDerivation rec {
  name = "homepage";
  src = ../.;
  nativeBuildInputs = with pkgs; [
    nodejs_24
    pnpm
    pnpmConfigHook
  ];
  pnpmDeps = pkgs.fetchPnpmDeps {
    inherit src;
    pname = name;
    pnpm = pkgs.pnpm;
    fetcherVersion = 2;
    hash = "sha256-ZYmk1WY/v/sGj7rQL19pMs8UOzJQNhrlDUJ8jgxIgPw=";
  };
  buildPhase = ''
    pnpm build
  '';
  installPhase = ''
    cp -r dist $out
    find "$out" -type f ! -name '*.etag' -print0 | while IFS= read -r -d "" file; do
      # yoinked from byte-sized-emi https://git.byte-sized.fyi/emilia/nix-config/src/branch/main/nix/packages/linktree/default.nix
      # compute md5 hash and write only the hash to a .etag file next to the original file
      md5sum "$file" | awk '{print $1}' > "$file.etag"
    done
  '';
}
