package com.github.ageofwar.ragna;

public record Model(Mesh mesh, Material material) {
    public Model withMesh(Mesh mesh) {
        return new Model(mesh, material);
    }

    public Model withMaterial(Material material) {
        return new Model(mesh, material);
    }

    public static Model[] skybox(Model[] model) {
        return skybox(model, 1);
    }

    public static Model[] skybox(Model[] model, float intensity) {
        var skybox = new Model[model.length];
        for (int i = 0; i < model.length; i++) {
            var texture = (Material.Texture) model[i].material();
            skybox[i] = model[i].withMaterial(new Material.SkyBox(texture.path(), texture.coordinates(), Color.WHITE, intensity));
        }
        return skybox;
    }
}
