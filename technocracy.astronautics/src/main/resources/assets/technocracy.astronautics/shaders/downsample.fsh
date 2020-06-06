#version 130
out vec4 FragColor;
in vec2 FragCoord;

uniform sampler2D image;
uniform vec2 pixelSize;

void main()
{
    vec2 vTexCoord = FragCoord.xy ;

    // need to use textureOffset here
    vec3 col0 = textureOffset(image, vTexCoord, ivec2( -2,  0 ) ).xyz;
    vec3 col1 = textureOffset(image, vTexCoord, ivec2(  2,  0 ) ).xyz;
    vec3 col2 = textureOffset(image, vTexCoord, ivec2(  0, -2 ) ).xyz;
    vec3 col3 = textureOffset(image, vTexCoord, ivec2(  0,  2 ) ).xyz;

    vec3 col = (col0+col1+col2+col3) * 0.25;

    FragColor = vec4( col.xyz, 1.0 );

}