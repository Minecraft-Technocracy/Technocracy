#version 120

uniform float farplane;
varying float flogz;
varying float fcoef_half;

void main() {

    float Fcoef = 2.0 / log2(farplane + 1.0);

    //gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    gl_FrontColor = gl_Color;

    //gl_Position.z = log2(max(1e-6, 1.0 + gl_Position.w * Fcoef_Fcoefhalf.x)) * Fcoef - 1.0;
    gl_Position.z = log2(max(1e-6, 1.0 + gl_Position.w)) * Fcoef - 1.0;
    fcoef_half = 0.5 * Fcoef;
    flogz = 1.0 + gl_Position.w;
}

