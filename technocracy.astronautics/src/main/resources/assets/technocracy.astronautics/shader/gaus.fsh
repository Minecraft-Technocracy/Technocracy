#version 130
out vec4 FragColor;

uniform sampler2D image;

uniform bool horizontal;
uniform bool expand;
uniform float expandFaktor;
float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

void main()
{
    vec2 tex_offset = 1.0 / textureSize(image, 0); // gets size of single texel
    vec3 result = texture(image, gl_TexCoord[0].st).rgb * weight[0]; // current fragment's contribution
    if(horizontal)
    {
        if(expand) {
            for (int i = 1; i < 5; ++i)
            {
                result += texture(image, gl_TexCoord[0].st + vec2(tex_offset.x * i * expandFaktor, 0.0)).rgb * weight[i];
                result += texture(image, gl_TexCoord[0].st - vec2(tex_offset.x * i * expandFaktor, 0.0)).rgb * weight[i];
            }
        } else {
            for (int i = 1; i < 5; ++i)
            {
                result += texture(image, gl_TexCoord[0].st + vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
                result += texture(image, gl_TexCoord[0].st - vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
            }
        }
    }
    else
    {
        if(expand) {
            for (int i = 1; i < 5; ++i)
            {
                result += texture(image, gl_TexCoord[0].st + vec2(0.0, tex_offset.y * i * expandFaktor)).rgb * weight[i];
                result += texture(image, gl_TexCoord[0].st - vec2(0.0, tex_offset.y * i * expandFaktor)).rgb * weight[i];
            }
        } else {
            for (int i = 1; i < 5; ++i)
            {
                result += texture(image, gl_TexCoord[0].st + vec2(0.0, tex_offset.y * i)).rgb * weight[i];
                result += texture(image, gl_TexCoord[0].st - vec2(0.0, tex_offset.y * i)).rgb * weight[i];
            }
        }
    }
    FragColor = vec4(result, 1.0);
}