#version 450

in vec3 outPosition;
in vec2 outTexturePos;
in vec3 outNormal;

out vec4 fragColor;

uniform sampler2D textureSampler;
uniform int specularPower;

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float reflectance;
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
uniform AmbientLight ambientLights[10];
uniform DirectionalLight directionalLights[100];
uniform PointLight pointLights[100];

vec4 ambientColor(AmbientLight light, vec4 ambient);
vec4 diffuseColor(DirectionalLight light, vec4 color, vec3 normal);
vec4 diffuseColor(PointLight light, vec4 color, vec3 normal, vec3 position);

void main()
{
    vec4 textureColor = (outTexturePos.x > -0.1 || outTexturePos.y > -0.1) ?
        texture(textureSampler, outTexturePos) :
        vec4(0,0,0,0);

    vec4 ambient = material.ambient + textureColor;
    vec4 diffuse = material.diffuse + textureColor;
    vec4 specular = material.specular + textureColor;
    float reflectance = material.reflectance;

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

    //vec4 specularComponent = specularColor(specular, vec3(1,1,1), 1, vec3(0,0,0), vec3(0,0,1), vec3(0,0,1), reflectance);

    fragColor = ambientComponent + diffuseComponent;
}

vec4 ambientColor(AmbientLight light, vec4 color) {
    return vec4(light.intensity * light.color, 1) * color;
}

vec4 diffuseColor(DirectionalLight light, vec4 color, vec3 normal) {
    return color * vec4(light.color, 1.0) * light.intensity * max(dot(normal, -light.direction), 0.0);
}

vec4 diffuseColor(PointLight light, vec4 color, vec3 normal, vec3 position) {
    vec3 toLight = light.position - position;
    vec3 lightDirection = normalize(toLight);
    float distance = length(toLight);
    float attenuation = light.attenuation.constant + light.attenuation.linear * distance + light.attenuation.quadratic * distance * distance;
    float intensity = light.intensity / max(attenuation, 1e-6);
    return color * vec4(light.color, 1.0) * intensity * max(dot(normal, lightDirection), 0.0);
}

vec4 specularColor(vec4 color, vec3 lightColor, float lightIntensity, vec3 position, vec3 toLightDir, vec3 normal, float reflectance) {
    vec3 cameraDirection = normalize(-position);
    vec3 fromLightDir = -toLightDir;
    vec3 reflectedLight = normalize(reflect(fromLightDir, normal));
    float specularFactor = pow(max(dot(cameraDirection, reflectedLight), 0.0), specularPower);
    return color * lightIntensity * specularFactor * reflectance * vec4(lightColor, 1.0);
}
