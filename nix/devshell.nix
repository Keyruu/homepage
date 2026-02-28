{ pkgs, perSystem, ... }:
perSystem.devshell.mkShell
  {
    packages = with pkgs; [
      nodejs_24
      pnpm_10
    ];

    commands = [ { package = pkgs.terraform; } ];
  }

  pkgs.mkShell
  {

    shellHook = ''
      export PS1="(homepage) $PS1"
    '';
  }
