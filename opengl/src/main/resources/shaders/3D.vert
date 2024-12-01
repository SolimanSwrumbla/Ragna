#version 450

layout (location=0) in vec3 position;
layout (location=1) in vec2 texturePos;
layout (location=2) in vec3 normal;

out vec2 outTexturePos;
out vec3 outNormal;
out vec3 outPosition;

uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main()
{
    vec4 modelPosition = modelMatrix * vec4(position, 1.0);
    gl_Position = viewMatrix * modelPosition;
    outPosition = modelPosition.xyz;
    outTexturePos = texturePos;
    outNormal = normalize(modelMatrix * vec4(normal, 0.0)).xyz;
}