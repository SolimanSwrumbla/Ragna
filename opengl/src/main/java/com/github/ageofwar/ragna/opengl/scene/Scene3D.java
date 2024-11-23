package com.github.ageofwar.ragna.opengl.scene;

import com.github.ageofwar.ragna.*;
import com.github.ageofwar.ragna.opengl.GlModel;
import com.github.ageofwar.ragna.opengl.GlModels;
import com.github.ageofwar.ragna.opengl.GlShaderProgram;
import com.github.ageofwar.ragna.opengl.GlShaders;

import java.util.ArrayList;
import java.util.Comparator;

import static org.lwjgl.opengl.GL30.*;

public class Scene3D implements Scene {
    private final ArrayList<GlEntity> solidEntities = new ArrayList<>();
    private final ArrayList<GlEntity> transparentEntities = new ArrayList<>();

    private Camera camera;

    public static Scene3D withEntities(Camera camera, Entity... entities) {
        var scene = new Scene3D(camera);
        scene.addEntities(entities);
        return scene;
    }

    public Scene3D(Camera camera) {
        this.camera = camera;
    }

    public void addEntities(Entity... entities) {
        for (Entity entity : entities) {
            var glEntity = new GlEntity(entity, GlModels.get(entity.model()));
            if (glEntity.model.isTransparent()) {
                transparentEntities.add(glEntity);
            } else {
                solidEntities.add(glEntity);
            }
        }
        zSort(transparentEntities);
    }

    public void setEntities(Entity... entities) {
        solidEntities.clear();
        transparentEntities.clear();
        addEntities(entities);
    }

    @Override
    public void render(Window window, long time) {
        var shaderProgram = GlShaders.getShaderProgram3D();
        GlShaderProgram.bind(shaderProgram);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glEnable(GL_CULL_FACE);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        var aspectRatio = window.aspectRatio();
        shaderProgram.setUniform("projectionMatrix", camera.matrix(aspectRatio));

        glCullFace(GL_BACK);
        for (var entity : solidEntities) {
            shaderProgram.setUniform("modelMatrix", entity.entity.transformMatrix());
            entity.model.render();
        }
        for (var entity : transparentEntities) {
            shaderProgram.setUniform("modelMatrix", entity.entity.transformMatrix());
            glCullFace(GL_FRONT);
            entity.model.render();
            glCullFace(GL_BACK);
            entity.model.render();
        }
    }

    private void zSort(ArrayList<GlEntity> transparentEntities) {
        transparentEntities.sort(Comparator.comparing(entity -> -Vector.norm(Matrix.productWithVector(Matrix.product(camera.viewMatrix()), entity.entity.position().vectorUniform()))));
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        zSort(transparentEntities);
    }

    public void setCameraPosition(Position position) {
        setCamera(camera.withPosition(position));
    }

    public void setCameraRotation(Rotation rotation) {
        setCamera(camera.withRotation(rotation));
    }

    record GlEntity(Entity entity, GlModel model) {
    }
}
