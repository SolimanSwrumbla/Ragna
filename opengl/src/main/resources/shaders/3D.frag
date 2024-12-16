#version 450

in vec3 outPosition;
in vec2 outAmbientTexture;
in vec2 outDiffuseTexture;
in vec2 outSpecularTexture;
in vec2 outEmissiveTexture;
in vec3 outNormal;

out vec4 fragColor;

uniform mat4 projectionMatrix;
uniform sampler2D ambientTextureSampler;
uniform sampler2D diffuseTextureSampler;
uniform sampler2D specularTextureSampler;
uniform sampler2D emissiveTextureSampler;

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    vec4 emissive;
    float reflectance;
    float specularPower;
};

struct Attenuation
{
    float constant;
    float linear;
    float quadratic;
};

struct AmbientLight
{
    vec3 color;
    float intensity;
};

struct PointLight {
    vec3 position;
    vec3 color;
    float intensity;
    Attenuation attenuation;
};

struct SpotLight
{
    PointLight pl;
    vec3 conedir;
    float cutoff;
};

struct DirectionalLight
{
    vec3 direction;
    vec3 color;
    float intensity;
};

uniform Material material;
uniform int ambientLightsSize;
uniform int directionalLightsSize;
uniform int pointLightsSize;
uniform vec3 cameraPosition;
uniform AmbientLight ambientLights[10];
uniform DirectionalLight directionalLights[100];
uniform PointLight pointLights[100];

vec4 ambientColor(AmbientLight light, vec4 ambient);
vec4 diffuseColor(DirectionalLight light, vec4 color, vec3 normal);
vec4 diffuseColor(PointLight light, vec4 color, vec3 normal, vec3 position);
vec4 specularColor(DirectionalLight light, vec4 color, float reflectance, float specularPower, vec3 normal, vec3 position);
vec4 specularColor(PointLight light, vec4 color, float reflectance, float specularPower, vec3 normal, vec3 position);
vec4 color(sampler2D textureSampler, vec2 textureCoords, vec4 color);

void main()
{
    vec4 textureColor = (outDiffuseTexture.x > -0.1 || outDiffuseTexture.y > -0.1) ?
        texture(diffuseTextureSampler, outDiffuseTexture) :
        vec4(0,0,0,0);

    vec4 ambient = color(ambientTextureSampler, outAmbientTexture, material.ambient);
    vec4 diffuse = color(diffuseTextureSampler, outDiffuseTexture, material.diffuse);
    vec4 specular = color(specularTextureSampler, outSpecularTexture, material.specular);
    vec4 emissive = color(emissiveTextureSampler, outEmissiveTexture, material.emissive);
    float reflectance = material.reflectance;
    float specularPower = material.specularPower;

    vec4 ambientComponent = vec4(0,0,0,1);
    for (int i = 0; i < ambientLightsSize; i++) {
        if (ambientLights[i].intensity > 0) {
            ambientComponent += ambientColor(ambientLights[i], ambient);
        }
    }

    vec4 diffuseComponent = vec4(0,0,0,1);
    for (int i = 0; i < directionalLightsSize; i++) {
        if (directionalLights[i].intensity > 0) {
            diffuseComponent += diffuseColor(directionalLights[i], diffuse, outNormal);
        }
    }
    for (int i = 0; i < pointLightsSize; i++) {
        if (pointLights[i].intensity > 0) {
            diffuseComponent += diffuseColor(pointLights[i], diffuse, outNormal, outPosition);
        }
    }

    vec4 specularComponent = vec4(0,0,0,1);
    for (int i = 0; i < directionalLightsSize; i++) {
        if (directionalLights[i].intensity > 0) {
            specularComponent += specularColor(directionalLights[i], specular, reflectance, specularPower, outNormal, outPosition);
        }
    }
    for (int i = 0; i < pointLightsSize; i++) {
        if (pointLights[i].intensity > 0) {
            specularComponent += specularColor(pointLights[i], specular, reflectance, specularPower, outNormal, outPosition);
        }
    }

    fragColor = ambientComponent + diffuseComponent + specularComponent + emissive;
}

vec4 ambientColor(AmbientLight light, vec4 color) {
    return vec4(light.intensity * light.color, 1) * color;
}

vec4 diffuseColor(DirectionalLight light, vec4 color, vec3 normal) {
    return color * vec4(light.color, 1.0) * light.intensity * max(dot(normal, -light.direction), 0.0);
}

vec4 diffuseColor(PointLight light, vec4 color, vec3 normal, vec3 position) {
    DirectionalLight dl;
    vec3 lightToPositionDirection = position - light.position;
    dl.direction = normalize(lightToPositionDirection);
    dl.color = light.color;
    float distance = length(lightToPositionDirection);
    float attenuation = light.attenuation.constant + light.attenuation.linear * distance + light.attenuation.quadratic * distance * distance;
    dl.intensity = light.intensity / max(attenuation, 1e-6);
    return diffuseColor(dl, color, normal);
}

vec4 specularColor(DirectionalLight light, vec4 color, float reflectance, float specularPower, vec3 normal, vec3 position) {
    vec3 positionCameraDirection = normalize(cameraPosition - position);
    vec3 reflectedDirection = reflect(light.direction, normal);
    float specularFactor = pow(max(dot(reflectedDirection, positionCameraDirection), 0.0), specularPower);
    return color * vec4(light.color, 1.0) * light.intensity * specularFactor * reflectance;
}

vec4 specularColor(PointLight light, vec4 color, float reflectance, float specularPower, vec3 normal, vec3 position) {
    DirectionalLight dl;
    dl.direction = normalize(position - light.position);
    dl.color = light.color;
    dl.intensity = light.intensity;
    return specularColor(dl, color, reflectance, specularPower, normal, position);
}

vec4 color(sampler2D textureSampler, vec2 textureCoords, vec4 color) {
    vec4 textureColor = (textureCoords.x > -0.1 || textureCoords.y > -0.1) ?
        texture(textureSampler, textureCoords) :
        vec4(0,0,0,0);
    return color + textureColor;
}