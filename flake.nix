{
  description = "P2P locality aware payment simulation";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
  };

  outputs = { self, nixpkgs }:
    let
      pkgs = import nixpkgs {
        system = "x86_64-linux";
        config.allowUnfree = true;
      };
    in {
      devShells.x86_64-linux.default = pkgs.mkShell {
        packages = [
          pkgs.jdk21
          pkgs.jetbrains.idea
        ];
      };
    };
}
