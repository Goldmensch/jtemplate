{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    flake-parts.url = "github:hercules-ci/flake-parts";
  };

  outputs = {
    self,
    flake-parts,
    ...
  } @ inputs:
    flake-parts.lib.mkFlake {inherit inputs;} {
      systems = ["x86_64-linux"];

      perSystem = {
        config,
        lib,
        pkgs,
        system,
        ...
      }: let
        javaVersion = 25;

        jdk = pkgs."temurin-bin-${toString javaVersion}";
       in {
         devShells.default = pkgs.mkShell {
           name = "Repository Setup";
           packages = with pkgs; [git maven jbang jdk];

           shellHook = ''
           jbang jdk install ${toString javaVersion} ${jdk}
           '';
         };
       };
    };
}