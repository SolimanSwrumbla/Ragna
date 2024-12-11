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
    private final ArrayList<Light.Ambient> ambientLights = new ArrayList<>();
    private final ArrayList<Light.Point> pointLights = new ArrayList<>();
    private final ArrayList<Light.Directional> directionalLights = new ArrayList<>();
    private Content content;
    private Camera camera;

    public Scene3D(Camera camera, Content content) {
        this.camera = camera;
        this.content = content;
    }

    public void addLights(Light... lights) {
        for (Light light : lights) {
            if (light instanceof Light.Ambient ambientLight) {
                ambientLights.add(ambientLight);
            } else if (light instanceof Light.Point pointLight) {
                pointLights.add(pointLight);
            } else if (light instanceof Light.Directional directionalLight) {
                directionalLights.add(directionalLight);
            }
        }
    }

    @Override
    public void render(Window window, long time) {
        var shaderProgramSkybox = GlShaders.getShaderProgramSkybox();
        var shaderProgram3D = GlShaders.getShaderProgram3D();

        var aspectRatio = window.aspectRatio();

        GlShaderProgram.bind(shaderProgram3D);
        shaderProgram3D.setUniformMatrix("viewMatrix", camera.matrix(aspectRatio));
        shaderProgram3D.setUniformVector("cameraPosition", camera.position().vector());
        shaderProgram3D.setUniform("ambientLightsSize", ambientLights.size());
        for (int i = 0; i < ambientLights.size(); i++) {
            shaderProgram3D.setUniform("ambientLights[" + i + "]", ambientLights.get(i));
        }
        shaderProgram3D.setUniform("directionalLightsSize", directionalLights.size());
        for (int i = 0; i < directionalLights.size(); i++) {
            shaderProgram3D.setUniform("directionalLights[" + i + "]", directionalLights.get(i));
        }
        shaderProgram3D.setUniform("pointLightsSize", pointLights.size());
        for (int i = 0; i < pointLights.size(); i++) {
            shaderProgram3D.setUniform("pointLights[" + i + "]", pointLights.get(i));
        }

        shaderProgram3D.setUniformMatrix("viewMatrix", camera.matrix(aspectRatio));

        GlShaderProgram.bind(shaderProgramSkybox);
        shaderProgramSkybox.setUniformMatrix("viewMatrix", camera.matrix(aspectRatio));

        var solidEntities = new ArrayList<GlEntity>();
        var transparentEntities = new ArrayList<GlEntity>();
        entities(time, solidEntities, transparentEntities);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDisable(GL_CULL_FACE);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        for (var entity : solidEntities) {
            entity.render();
        }

        glEnable(GL_CULL_FACE);
        for (var entity : transparentEntities) {
            glCullFace(GL_FRONT);
            entity.render();
            glCullFace(GL_BACK);
            entity.render();
        }
    }

    private void entities(long time, ArrayList<GlEntity> solidEntities, ArrayList<GlEntity> transparentEntities) {
        for (var entity : content.entities(camera, time)) {
            for (var model : entity.model()) {
                var glModel = GlModels.get(model);
                if (glModel.isTransparent()) {
                    transparentEntities.add(new GlEntity(entity, glModel));
                } else {
                    solidEntities.add(new GlEntity(entity, glModel));
                }
            }
        }
        depthSort(transparentEntities);
    }

    private void depthSort(ArrayList<GlEntity> transparentEntities) {
        transparentEntities.sort(Comparator.comparing(entity -> -Vector.norm(Matrix.productWithVector(Matrix.product(camera.viewMatrix()), entity.entity.position().vectorUniform()))));
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setCameraPosition(Position position) {
        setCamera(camera.withPosition(position));
    }

    public void setCameraRotation(Rotation rotation) {
        setCamera(camera.withRotation(rotation));
    }

    private record GlEntity(Entity entity, GlModel model) {
        public void render() {
            model.render(entity.transformMatrix());
        }
    }

    @FunctionalInterface
    public interface Content {
        Iterable<Entity> entities(Camera camera, long time);
    }
}
