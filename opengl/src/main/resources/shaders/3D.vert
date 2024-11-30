#version 450

layout (location=0) in vec3 position;
layout (location=1) in vec2 texturePos;
layout (location=2) in vec3 normal;

out vec2 outTexturePos;
out vec3 outNormal;
out vec3 outPosition;

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;

void main()
{
    gl_Position = projectionMatrix * modelMatrix * vec4(position, 1.0);
    outPosition = (modelMatrix * vec4(position, 1.0)).xyz;
    outTexturePos = texturePos;
    outNormal = normal;
}