#version 150

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

in Particle
{
    vec4 mt_ct_rt_rot;
    mat4 modelMatrix;
} particle[];

out vec4 mt_ct_rt_rot;
out vec2 textureCoords;

void main (void) {
    mt_ct_rt_rot = particle[0].mt_ct_rt_rot;
    mat4 modelMatrix = particle[0].modelMatrix;

    // a: left-bottom
    vec2 va = vec2(-0.5196, 1.1906);
    gl_Position = modelMatrix * vec4(va, 0, 1);
    textureCoords = vec2(0, 0);
    EmitVertex();

    // b: left-top
    vec2 vb = vec2(-1.2804, -0.4453);
    gl_Position = modelMatrix * vec4(vb, 0, 1);
    textureCoords = vec2(0, 1);
    EmitVertex();

    // d: right-bottom
    vec2 vd = vec2(1.1137, 0.5372);
    gl_Position = modelMatrix * vec4(vd, 0, 1);
    textureCoords = vec2(1, 0);
    EmitVertex();

    // c: right-top
    vec2 vc = vec2(0.5506, -1.3691);
    gl_Position = modelMatrix * vec4(vc, 0, 1);
    textureCoords = vec2(1, 1);
    EmitVertex();

    EndPrimitive();
}