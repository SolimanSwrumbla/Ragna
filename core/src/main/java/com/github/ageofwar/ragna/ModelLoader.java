package com.github.ageofwar.ragna;

import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.assimp.Assimp.*;

public class ModelLoader {
    private ModelLoader() {
    }

    public static Model[] load(String mesh) {
        return load(mesh, aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights |
                aiProcess_PreTransformVertices);
    }

    private static Model[] load(String mesh, int flags) {
        try (var scene = aiImportFile(mesh, flags)) {
            if (scene == null) {
                throw new RuntimeException("Error loading model [resource: " + mesh + "]");
            }
            var modelDir = mesh.substring(0, mesh.lastIndexOf('/') + 1);
            return load(scene, modelDir);
        }
    }

    private static Model[] load(AIScene scene, String modelDir) {
        var models = new Model[scene.mNumMeshes()];
        var aiMeshes = scene.mMeshes();
        var aiMaterials = scene.mMaterials();
        assert aiMeshes != null && aiMaterials != null;
        for (int i = 0; i < models.length; i++) {
            try (var aiMesh = AIMesh.create(aiMeshes.get(i)); var aiMaterial = AIMaterial.create(aiMaterials.get(aiMesh.mMaterialIndex()))) {
                var mesh = load(aiMesh);
                var material = load(aiMaterial, aiMesh, modelDir);
                models[i] = new Model(mesh, material);
            }
        }
        return models;
    }

    private static Mesh load(AIMesh aiMesh) {
        var vertices = loadVertices(aiMesh);
        var normals = loadNormals(aiMesh);
        var indices = loadIndices(aiMesh);
        return new Mesh(vertices, normals, indices);
    }

    private static float[] loadVertices(AIMesh aiMesh) {
        var buffer = aiMesh.mVertices();
        var data = new float[buffer.remaining() * 3];
        var pos = 0;
        while (buffer.remaining() > 0) {
            var coord = buffer.get();
            data[pos++] = coord.x();
            data[pos++] = coord.y();
            data[pos++] = coord.z();
        }
        return data;
    }

    private static int[] loadIndices(AIMesh aiMesh) {
        var indices = new ArrayList<Integer>();
        var numFaces = aiMesh.mNumFaces();
        var aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            var aiFace = aiFaces.get(i);
            var buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
        return toIntArray(indices);
    }

    private static float[] loadTextCoords(AIMesh aiMesh) {
        var buffer = aiMesh.mTextureCoords(0);
        if (buffer == null) return new float[0];
        var data = new float[buffer.remaining() * 2];
        var pos = 0;
        while (buffer.remaining() > 0) {
            var coord = buffer.get();
            data[pos++] = coord.x();
            data[pos++] = 1 - coord.y();
        }
        return data;
    }

    private static float[] loadNormals(AIMesh aiMesh) {
        var buffer = aiMesh.mNormals();
        if (buffer == null) return new float[0];
        var data = new float[buffer.remaining() * 3];
        var pos = 0;
        while (buffer.remaining() > 0) {
            var coord = buffer.get();
            data[pos++] = coord.x();
            data[pos++] = coord.y();
            data[pos++] = coord.z();
        }
        return data;
    }

    private static Material load(AIMaterial aiMaterial, AIMesh aiMesh, String modelDir) {
        var textCoords = loadTextCoords(aiMesh);
        float[] ambientColor = new float[4];
        float[] diffuseColor = new float[4];
        float[] specularColor = new float[4];
        String texture = null;
        try (var stack = MemoryStack.stackPush()) {
            var color = AIColor4D.create();

            int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, color);
            if (result == aiReturn_SUCCESS) {
                ambientColor = new float[] { color.r(), color.g(), color.b(), color.a() };
            }

            result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);
            if (result == aiReturn_SUCCESS) {
                diffuseColor = new float[] { color.r(), color.g(), color.b(), color.a() };
            }

            result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, color);
            if (result == aiReturn_SUCCESS) {
                specularColor = new float[] { color.r(), color.g(), color.b(), color.a() };
            }

            var reflectance = new float[1];
            aiGetMaterialFloatArray(aiMaterial, AI_MATKEY_SHININESS_STRENGTH, aiTextureType_NONE, 0, reflectance, new int[] { 1 });

            var aiTexturePath = AIString.calloc(stack);
            result = aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null, null, null, null, null, null);

            if (result == aiReturn_SUCCESS) {
                String texturePath = aiTexturePath.dataString();
                if (!texturePath.isEmpty()) {
                    if (modelDir.isEmpty()) {
                        texture = texturePath;
                    } else {
                        texture = modelDir + "/" + texturePath;
                    }
                }
            }

            if (texture != null) return new Material.Texture(texture, textCoords, Color.rgba(ambientColor), Color.rgba(diffuseColor), Color.rgba(specularColor), reflectance[0]);
            return new Material.Fill(Color.rgba(ambientColor), Color.rgba(diffuseColor), Color.rgba(specularColor), reflectance[0]);
        }
    }

    private static int[] toIntArray(ArrayList<Integer> list) {
        var array = new int[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}
