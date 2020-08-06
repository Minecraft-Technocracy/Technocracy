#version 120

varying vec2 FragCoord;
varying vec3 VertexCoord;

void main() {
    FragCoord = gl_MultiTexCoord0.st ;
    VertexCoord = gl_Vertex.xyz;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}