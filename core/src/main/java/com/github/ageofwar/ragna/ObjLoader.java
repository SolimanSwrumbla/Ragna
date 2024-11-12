package com.github.ageofwar.ragna;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Objects;

public class ObjLoader {
    public static Mesh loadResource(String mesh) {
        try (var reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(ObjLoader.class.getClassLoader().getResourceAsStream(mesh))))) {
            return load(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Model loadResource(String mesh, String texture) {
        try (var reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(ObjLoader.class.getClassLoader().getResourceAsStream(mesh))))) {
            return loadResource(reader, texture);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Mesh load(BufferedReader reader) {
        var vertices = new ArrayList<Float>();
        var indices = new ArrayList<Integer>();
        reader.lines().forEach(line -> {
            if (line.isBlank()) return;
            if (line.startsWith("#")) return;
            var parts = line.split("\\s+");
            switch (parts[0]) {
                case "v" -> {
                    vertices.add(Float.parseFloat(parts[1]));
                    vertices.add(Float.parseFloat(parts[2]));
                    vertices.add(Float.parseFloat(parts[3]));
                }
                case "f" -> {
                    for (int i = 1; i < parts.length; i++) {
                        var indicesParts = parts[i].split("/");
                        indices.add(Integer.parseInt(indicesParts[0]) - 1);
                    }
                }
            }
        });
        return new Mesh(toFloatArray(vertices), toIntArray(indices));
    }

    public static Model loadResource(BufferedReader reader, String texture) {
        var vertices = new ArrayList<Float>();
        var indices = new ArrayList<Integer>();
        var textureCoords = new ArrayList<Float>();
        var textureIndices = new ArrayList<Integer>();
        reader.lines().forEach(line -> {
            if (line.isBlank()) return;
            if (line.startsWith("#")) return;
            var parts = line.split("\\s+");
            switch (parts[0]) {
                case "v" -> {
                    vertices.add(Float.parseFloat(parts[1]));
                    vertices.add(Float.parseFloat(parts[2]));
                    vertices.add(Float.parseFloat(parts[3]));
                }
                case "f" -> {
                    for (int i = 1; i < parts.length; i++) {
                        var indicesParts = parts[i].split("/");
                        indices.add(Integer.parseInt(indicesParts[0]) - 1);
                        if (indicesParts.length > 1 && !indicesParts[1].isEmpty()) {
                            textureIndices.add(Integer.parseInt(indicesParts[1]) - 1);
                        }
                    }
                }
                case "vt" -> {
                    textureCoords.add(Float.parseFloat(parts[1]));
                    textureCoords.add(1f - Float.parseFloat(parts[2]));
                }
            }
        });
        return new Model(new Mesh(toFloatArray(vertices), toIntArray(indices)), new Material.TextureFromResource(texture, toFloatArray(textureCoords, textureIndices, indices)));
    }

    private static float[] toFloatArray(ArrayList<Float> list) {
        var array = new float[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private static float[] toFloatArray(ArrayList<Float> list, ArrayList<Integer> textureIndices, ArrayList<Integer> indices) {
        var array = new float[indices.size() * 2];
        for (int i = 0; i < indices.size(); i++) {
            var index = textureIndices.get(i);
            array[indices.get(i) * 2] = list.get(index * 2);
            array[indices.get(i) * 2 + 1] = list.get(index * 2 + 1);
        }
        return array;
    }

    private static int[] toIntArray(ArrayList<Integer> list) {
        var array = new int[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}
