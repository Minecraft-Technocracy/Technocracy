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

    /*float far = 128;
    float C = Fcoef_Fcoefhalf.x;

    float FC = 1.0/log(far*C + 1);

    //logz = gl_Position.w*C + 1;  //version with fragment code
    flogz = log(gl_Position.w*C + 1)*FC;
    gl_Position.z = (2*flogz - 1)*gl_Position.w;
    gl_FrontColor = gl_Color;

    /*gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    gl_Position.z = log2(max(1e-6, 1.0 + gl_Position.w)) * Fcoef_Fcoefhalf.x - 1.0;

    //out float flogz;
    flogz = 1.0 + gl_Position.w;*/
}

