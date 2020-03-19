#version 130
out vec4 FragColor;
in vec2 FragCoord;

uniform sampler2D image;
uniform vec3 u_xyPixelSize_zIteration;

vec3 KawaseBlurFilter(sampler2D tex, vec2 texCoord, vec2 pixelSize, float iteration)
{
    vec2 texCoordSample;
    vec2 halfPixelSize = pixelSize / 2.0f;
    vec2 dUV = (pixelSize.xy * vec2(iteration, iteration)) + halfPixelSize.xy;

    vec3 cOut;

    // Sample top left pixel
    texCoordSample.x = texCoord.x - dUV.x;
    texCoordSample.y = texCoord.y + dUV.y;

    cOut = texture(tex, texCoordSample).xyz;

    // Sample top right pixel
    texCoordSample.x = texCoord.x + dUV.x;
    texCoordSample.y = texCoord.y + dUV.y;

    cOut += texture(tex, texCoordSample).xyz;

    // Sample bottom right pixel
    texCoordSample.x = texCoord.x + dUV.x;
    texCoordSample.y = texCoord.y - dUV.y;
    cOut += texture(tex, texCoordSample).xyz;

    // Sample bottom left pixel
    texCoordSample.x = texCoord.x - dUV.x;
    texCoordSample.y = texCoord.y - dUV.y;

    cOut += texture(tex, texCoordSample).xyz;

    // Average
    cOut *= 0.25f;

    return cOut;
}

void main()
{
    //vec3 col = texture(uTex0, vTexCoord).xyz;
    FragColor.xyz = KawaseBlurFilter(image, FragCoord, u_xyPixelSize_zIteration.xy, u_xyPixelSize_zIteration.z);

    // double-Kawase is also an option, but loses some quality
    //FragColor.xyz += KawaseBlurFilter(image, gl_TexCoord[0].st, u_xyPixelSize_zIteration.xy, u_xyPixelSize_zIteration.z*2.0 + 1.0);
    //FragColor.xyz *= 0.5;

    FragColor.w = 1.0;
}