#version 130

varying float flogz;
varying float fcoef_half;
out vec4 FragColor;

void main()
{
    FragColor = gl_Color;
    gl_FragDepth = log2(flogz) * fcoef_half;
    //gl_FragDepth = log2(flogz) * Fcoef_Fcoefhalf.y;
}