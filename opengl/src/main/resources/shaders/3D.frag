#version 450

in vec4 outColor;
in vec2 outTexturePos;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main()
{
    vec4 textureColor = (outTexturePos.x > -0.1 || outTexturePos.y > -0.1) ?
        texture(textureSampler, outTexturePos) :
        vec4(0,0,0,0);
    fragColor = textureColor + outColor * (1 - textureColor.a);
}