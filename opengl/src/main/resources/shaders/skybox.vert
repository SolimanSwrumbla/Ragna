#version 450

layout (location=0) in vec3 position;
layout (location=1) in vec2 texturePos;

out vec2 outTexturePos;

uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main()
{
    gl_Position = viewMatrix * modelMatrix * vec4(position, 1.0);
    outTexturePos = texturePos;
}