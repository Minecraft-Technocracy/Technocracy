#version 120

varying vec4 vertexPos;
varying vec2 FragCoord;

void main()
{
    //normalDirection = normalize(vec3(
   // vec4(gl_Normal, 0.0) * modelMatrixInverse));
    //viewDirection = vec3(modelMatrix * gl_Vertex - vec4(cameraWorldPos, 1.0));

    //vec4 clipSpace = projectionMatrix * ( modelViewMatrix * cameraWorldPos );

    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    FragCoord = gl_MultiTexCoord0.st;
    vertexPos = gl_Position;
}