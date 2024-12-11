#version 450

in vec2 outTexturePos;
out vec4 fragColor;

uniform sampler2D textureSampler;
uniform vec3 color;
uniform float intensity;

void main()
{
    vec4 textureColor = (outTexturePos.x > -0.1 || outTexturePos.y > -0.1) ?
        texture(textureSampler, outTexturePos) :
        vec4(0,0,0,0);

    fragColor = vec4(intensity * color, 1) * textureColor;
}