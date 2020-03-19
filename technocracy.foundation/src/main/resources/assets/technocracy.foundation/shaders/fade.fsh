#version 130
out vec4 fragColor;
in vec2 FragCoord;
in vec4 worldCoord;

uniform sampler2D image;

vec3 lerp(vec3 a, vec3 b, float w)
{
    return a + w*(b-a);
}

void main()
{
    vec4 result = gl_Color * texture(image, FragCoord).rgba;

    vec3 pos = worldCoord.xyz; //+ vec3(-0.0,0.,-3);

    float distance = 1 - (sqrt(6.5) - sqrt(pos.x * pos.x + pos.y * pos.y + pos.z * pos.z));

    float alpha = max(0.0, min(distance, result.a));

    float blendFactor = step(3.0f, dot(gl_Color.rgb, gl_Color.rgb));
    fragColor = vec4(result.r , result.g , result.b , result.a * min(0.2, distance));

    //float blendFactor = saturate(1.0f - sharpness * (dot(gl_Color.rgb, gl_Color.rgb) - 3.0f));
    //fragColor = vec4(lerp(result.rgb, gl_Color.rgb, blendFactor) , alpha);
}