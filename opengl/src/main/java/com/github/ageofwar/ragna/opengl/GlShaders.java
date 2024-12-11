package com.github.ageofwar.ragna.opengl;

public class GlShaders {
    private static GlShader vertexShader3D;
    private static GlShader fragmentShader3D;
    private static GlShader vertexShaderSkybox;
    private static GlShader fragmentShaderSkybox;

    private static GlShaderProgram shaderProgram3D;
    private static GlShaderProgram shaderProgramSkybox;

    private GlShaders() {
    }

    public static GlShader getVertexShader3D() {
        if (vertexShader3D == null) {
            vertexShader3D = GlShader.loadVertexFromResources("shaders/3D.vert");
        }
        return vertexShader3D;
    }

    public static GlShader getFragmentShader3D() {
        if (fragmentShader3D == null) {
            fragmentShader3D = GlShader.loadFragmentFromResources("shaders/3D.frag");
        }
        return fragmentShader3D;
    }

    public static GlShaderProgram getShaderProgram3D() {
        if (shaderProgram3D == null) {
            shaderProgram3D = GlShaderProgram.create(getVertexShader3D(), getFragmentShader3D());
            GlShaderProgram.bind(shaderProgram3D);
            shaderProgram3D.setUniform("textureSampler", 0);
            shaderProgram3D.setUniform("specularPower", 100f);
        }
        return shaderProgram3D;
    }

    public static GlShader getVertexShaderSkybox() {
        if (vertexShaderSkybox == null) {
            vertexShaderSkybox = GlShader.loadVertexFromResources("shaders/skybox.vert");
        }
        return vertexShaderSkybox;
    }

    public static GlShader getFragmentShaderSkybox() {
        if (fragmentShaderSkybox == null) {
            fragmentShaderSkybox = GlShader.loadFragmentFromResources("shaders/skybox.frag");
        }
        return fragmentShaderSkybox;
    }

    public static GlShaderProgram getShaderProgramSkybox() {
        if (shaderProgramSkybox == null) {
            shaderProgramSkybox = GlShaderProgram.create(getVertexShaderSkybox(), getFragmentShaderSkybox());
            GlShaderProgram.bind(shaderProgramSkybox);
            shaderProgramSkybox.setUniform("textureSampler", 0);
        }
        return shaderProgramSkybox;
    }

    public static void close() {
        if (shaderProgram3D != null) {
            shaderProgram3D.close();
        }
        if (vertexShader3D != null) {
            vertexShader3D.close();
        }
        if (fragmentShader3D != null) {
            fragmentShader3D.close();
        }
        if (shaderProgramSkybox != null) {
            shaderProgramSkybox.close();
        }
        if (vertexShaderSkybox != null) {
            vertexShaderSkybox.close();
        }
        if (fragmentShaderSkybox != null) {
            fragmentShaderSkybox.close();
        }
    }
}
