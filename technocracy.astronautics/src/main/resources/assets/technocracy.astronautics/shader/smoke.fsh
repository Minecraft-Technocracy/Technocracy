#version 140

in vec2 textureCoords;

uniform sampler2D sampler0;
uniform sampler2D sampler1;
uniform float time;

void main(void){
    vec4 texel0 = texture(sampler0, textureCoords);

    vec4 texel1 = texture(sampler1, textureCoords + time);
    vec4 texel2 = texture(sampler1, textureCoords * 0.5 - time);
    vec4 texel3 = texture(sampler1, textureCoords * 2 + time);

    gl_FragColor = texel0;
    gl_FragColor.a = texel0.a * texel1.a * 2.0 * texel2.a * 2.0 * texel3.a * 2.0;
}