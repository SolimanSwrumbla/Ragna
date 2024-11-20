package com.github.ageofwar.ragna.opengl;

import com.github.ageofwar.ragna.Model;

import java.util.HashMap;

public class GlModels {
    private static final HashMap<Model, GlModel> models = new HashMap<>();

    private GlModels() {
    }

    public static GlModel get(Model model) {
        return models.computeIfAbsent(model, GlModel::create);
    }

    public static void close() {
        for (var model : models.values()) {
            model.close();
        }
    }
}
