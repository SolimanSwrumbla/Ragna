#version 450

layout (location=0) in vec3 position;
layout (location=1) in vec4 color;
// layout (location=2) in vec2 texturePos;

out vec4 outColor;
out vec2 outTexturePos;

uniform mat4 projectionMatrix;

void main()
{
    gl_Position = projectionMatrix * vec4(position, 1.0);
    outColor = color;
    // outTexturePos = texturePos;
}