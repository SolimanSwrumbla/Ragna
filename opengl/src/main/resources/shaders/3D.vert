#version 450

layout (location=0) in vec3 position;
layout (location=1) in vec2 ambientTexture;
layout (location=2) in vec2 diffuseTexture;
layout (location=3) in vec2 specularTexture;
layout (location=4) in vec2 emissiveTexture;
layout (location=5) in vec3 normal;

out vec2 outAmbientTexture;
out vec2 outDiffuseTexture;
out vec2 outSpecularTexture;
out vec2 outEmissiveTexture;
out vec3 outNormal;
out vec3 outPosition;

uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main()
{
    vec4 modelPosition = modelMatrix * vec4(position, 1.0);
    gl_Position = viewMatrix * modelPosition;
    outPosition = modelPosition.xyz;
    outAmbientTexture = ambientTexture;
    outDiffuseTexture = diffuseTexture;
    outSpecularTexture = specularTexture;
    outEmissiveTexture = emissiveTexture;
    outNormal = normalize(modelMatrix * vec4(normal, 0.0)).xyz;
}