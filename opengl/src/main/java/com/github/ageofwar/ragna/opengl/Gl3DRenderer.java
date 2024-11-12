package com.github.ageofwar.ragna.opengl;

import com.github.ageofwar.ragna.Camera;
import com.github.ageofwar.ragna.Matrix;
import com.github.ageofwar.ragna.Vector;

import java.util.ArrayList;
import java.util.Comparator;

import static org.lwjgl.opengl.GL11.*;

public class Gl3DRenderer {
    private final ArrayList<GlModel> solidModels;
    private final ArrayList<GlModel> transparentModels;

    public Gl3DRenderer() {
        solidModels = new ArrayList<>();
        transparentModels = new ArrayList<>();
    }

    public void addModel(GlModel model) {
        if (model.isTransparent()) {
            transparentModels.add(model);
        } else {
            solidModels.add(model);
        }
    }

    public void updateView(Camera camera) {
        transparentModels.sort(Comparator.comparing(model -> -Vector.dotProduct(Matrix.productWithVector(camera.matrix(16f/9f), model.firstVertexUniform()), camera.rotation().toVector())));
    }

    public void render() {
        for (var model : solidModels) {
            model.render();
        }
        glEnable(GL_CULL_FACE);
        for (var model : transparentModels) {
            glCullFace(GL_FRONT);
            model.render();
            glCullFace(GL_BACK);
            model.render();
        }
        glDisable(GL_CULL_FACE);
    }
}
