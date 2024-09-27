package com.github.ageofwar.ragna.opengl;

import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL30.*;

public record GlShaderProgram(int id) implements AutoCloseable {
    public static void bind(GlShaderProgram program) {
        glUseProgram(program.id);
    }

    public static void unbind() {
        glUseProgram(0);
    }

    public static GlShaderProgram create() {
        var programId = glCreateProgram();
        if (programId == 0) {
            throw new RuntimeException("Could not create Shader");
        }
        return new GlShaderProgram(programId);
    }

    public static GlShaderProgram create(GlShader... shaders) {
        var program = create();
        for (var shader : shaders) {
            glAttachShader(program.id, shader.shaderId());
        }
        program.link();
        for (var shader : shaders) {
            program.detach(shader);
        }
        return program;
    }

    public void setUniform(String name, int value) {
        glUniform1i(glGetUniformLocation(id, name), value);
    }

    public void setUniform(String name, float[] value) {
        glUniformMatrix4fv(glGetUniformLocation(id, name), true, value);
    }

    public void link() {
        glLinkProgram(id);
        if (glGetProgrami(id, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(id, 1024));
        }
    }

    public void detach(GlShader shader) {
        glDetachShader(id, shader.shaderId());
    }

    public void validate() {
        glValidateProgram(id);
        if (glGetProgrami(id, GL_VALIDATE_STATUS) == 0) {
            throw new RuntimeException("Error validating Shader code: " + glGetProgramInfoLog(id, 1024));
        }
    }

    @Override
    public void close() {
        if (id != 0) {
            glDeleteProgram(id);
        }
    }
}