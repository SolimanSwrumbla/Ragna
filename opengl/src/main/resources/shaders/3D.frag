#version 450

in vec4 outColor;
// in  vec2 outTexturePos;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main()
{
    fragColor = outColor;
}