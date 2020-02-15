#version 120

varying vec2 FragCoord;

void main() {
    FragCoord = gl_MultiTexCoord0.st;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}