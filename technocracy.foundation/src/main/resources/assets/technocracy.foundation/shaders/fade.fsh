#version 130
out vec4 fragColor;
in vec2 FragCoord;
in vec4 worldCoord;

uniform sampler2D image;
uniform bool alphaClip;
uniform vec2 screenSize;

mat4 screendoor = mat4(1.0 / 17.0, 9.0 / 17.0, 3.0 / 17.0, 11.0 / 17.0,
13.0 / 17.0, 5.0 / 17.0, 15.0 / 17.0, 7.0 / 17.0,
4.0 / 17.0, 12.0 / 17.0, 2.0 / 17.0, 10.0 / 17.0,
16.0 / 17.0, 8.0 / 17.0, 14.0 / 17.0, 6.0 / 17.0);

void main()
{

    vec2 screenCoord = ((worldCoord.xy/worldCoord.w)*0.5+0.5) * screenSize;

    float distance = worldCoord.z - 0.1;

    vec4 result = gl_Color * texture(image, FragCoord).rgba;

    if (alphaClip && result.a <= 0.5)
        discard;

    if (distance - (screendoor[int(screenCoord.x) % 4][int(screenCoord.y) % 4] * 3.0) < 0)
        discard;

    fragColor = result;

    //vec3 pos = worldCoord.xyz;

    //float distance = 1 - (sqrt(6.65) - sqrt(pos.x * pos.x + pos.y * pos.y + pos.z * pos.z));

    //float alpha = max(0.0, min(distance, result.a));
    //float blendFactor = step(3.0f, dot(gl_Color.rgb, gl_Color.rgb));
    //fragColor = vec4(result.r, result.g, result.b, result.a * min(0.7, distance));

    //float blendFactor = saturate(1.0f - sharpness * (dot(gl_Color.rgb, gl_Color.rgb) - 3.0f));
    //fragColor = vec4(lerp(result.rgb, gl_Color.rgb, blendFactor) , alpha);
}