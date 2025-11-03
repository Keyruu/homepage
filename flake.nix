{
  description = "Development environment for homepage project";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs =
    {
      self,
      nixpkgs,
      flake-utils,
    }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
      in
      {
        devShells.default = pkgs.mkShell {
          buildInputs = with pkgs; [
            jdk21
            sbt
            nodejs_22
            pnpm
            git
            # graalvm-ce
          ];

          JAVA_HOME = "${pkgs.jdk21}";
          SBT_OPTS = "-Xmx2G -XX:+UseG1GC";
        };
      }
    );
}
