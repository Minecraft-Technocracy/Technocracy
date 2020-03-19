#version 120

varying vec2 FragCoord;
varying vec4 worldCoord;

void main() {
    gl_FrontColor = gl_Color;
    FragCoord = gl_MultiTexCoord0.st;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    worldCoord = gl_Position;
}