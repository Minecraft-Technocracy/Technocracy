#version 130

in vec2 FragCoord;
out vec4 FragColor;

uniform sampler2D image;
uniform sampler2D image_normal;
uniform sampler2D image_diffuse;
uniform vec2 time;

uniform bool colorShift;
uniform float colorShiftAmount;

in vec4 vertexPos;

vec2 resolution = textureSize(image_normal, 0);
vec2 texel = 1.0 / resolution;

//Used to shift the blue color to a more red one
vec3 hueShift(vec3 color, float hue)
{
    const vec3 k = vec3(0.57735, 0.57735, 0.57735);
    float cosAngle = cos(hue);
    return vec3(color * cosAngle + cross(k, color) * sin(hue) + k * dot(k, color) * (1.0 - cosAngle));
}

void main()
{
    vec2 screenCoord = (vertexPos.xy/vertexPos.w)*0.5+0.5;

    //vec2 textureRatio = textureSize(image_normal, 0) / textureSize(image, 0);

    vec2 scaledCoord = FragCoord * 10;
    vec2 scaledOffset = time * texel;

    vec4 nt1 = texture(image_normal, scaledCoord + scaledOffset.xy);
    vec4 nt2 = texture(image_normal, scaledCoord - scaledOffset.yx);

    vec4 dif1 = texture(image_diffuse, scaledCoord + scaledOffset.xy);
    vec4 dif2 = texture(image_diffuse, scaledCoord - scaledOffset.yx);

    vec4 nt = mix(nt1, nt2, 0.5);
    vec4 dif = mix(dif1, dif2, 0.5);

    if (colorShift) {
        dif.rgb = hueShift(dif.rgb, colorShiftAmount);
    }

    vec4 pickedColor = texture(image, screenCoord + ((nt.xy - vec2(0.5))) * texel * 15.0);
    pickedColor.rgb += dif.rgb * dif.aaa;


    FragColor = pickedColor;

    //FragColor = mix(pickedColor, vec4(165 / 255.0, 132 / 255.0, 193 / 255.0, 0.1), 0.5);//mix(texture(image, coord + ((nt.xy - vec2(0.5)) * texel) * 10.2),  vec4(165 / 255.0, 132 / 255.0, 193 / 255.0, nt.xyz / 3.0 + 0.1), 1.0)  /* vec4(nt.r , nt.g , nt.b , 1.0)*/ /*+ vec4(nt.r * 0.25, nt.g * 0.25, nt.b * 0.25, 0.0)*/;

    //FragColor.rgb += (1 - ((nt.r + nt.g + nt.b) / 6.0) - 0.5) * vec3(0.1, 0.2, 0.9);

    //FragColor *= vec4(nt.r, nt.g, nt.b, 1.0);

    //texel *= 1.4;

    //if (FragCoord.x <= texel.x || FragCoord.x >= 1.0 - texel.x || FragCoord.y < texel.y || FragCoord.y >= 1.0 - texel.y) {
    //    FragColor = vec4(247/255.0, 205/255.0, 232/255.0, 0.8);
    //}
}