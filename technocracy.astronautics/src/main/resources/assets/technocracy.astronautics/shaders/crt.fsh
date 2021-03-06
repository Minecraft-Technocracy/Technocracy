#version 130

uniform vec2 scalar = vec2(12.0, 12.0);
uniform sampler2D sampler;

//color offset rgb
uniform vec3 ConvergeX = vec3(-1.0,0.0,1.0);
uniform vec3 ConvergeY = vec3(0.0, -1.0, 1.0);

// Hardness of scanline.
//  -8.0 = soft
// -16.0 = medium
uniform float hardScan=-2.0;

// Display warp.
// 0.0 = none
// 1.0/8.0 = extreme
uniform vec2 warp=vec2(1.0/16.0, 1.0/16.0);

// Amount of shadow mask.
uniform float maskDark=0.5;
uniform float maskLight=1.0;
uniform float pixelScaler=1.5;

//saturationboost
uniform float saturation=4.0;

vec2 resolution = textureSize(sampler, 0);
vec2 texel = 1.0 / resolution;
vec2 scaledResolution = resolution / scalar;
vec2 scaledPixelResolution = resolution / pixelScaler;

in vec2 FragCoord;
out vec4 FragColor;

//------------------------------------------------------------------------

vec3 colorCorrection(vec3 color) {
    float Luma = dot(color, vec3(0.3, 0.59, 0.11));
    vec3 Chroma = color - Luma;
    return (Chroma * saturation) + Luma;
}

//apply color deconverge and color correction
vec3 deconverge(vec2 pos) {
    vec3 CoordX = vec3(pos.x);
    vec3 CoordY = vec3(pos.y);

    CoordX = ConvergeX * texel.x + CoordX;
    CoordY = ConvergeY * texel.y + CoordY;

    float r   = texture(sampler, vec2(CoordX.x, CoordY.x), -16.0).r;
    float g = texture(sampler, vec2(CoordX.y, CoordY.y), -16.0).g;
    float b  = texture(sampler, vec2(CoordX.z, CoordY.z), -16.0).b;

    return colorCorrection(vec3(r, g, b));
}


// Nearest emulated sample given floating point position and texel offset.
// Also zero's off screen.
vec4 Fetch(vec2 pos) {
    pos=floor(pos*resolution)/resolution;


    if (any(greaterThan(abs(pos - 0.5), vec2(0.5))))
    return vec4(0.0);

    //return vec4(ToLinear(texture(sampler, pos.xy, -16.0).rgb), 1.0);
    return vec4((deconverge(pos)), 1.0);
}

// Distance in emulated pixels to nearest texel.
vec2 Dist(vec2 pos){
    pos=pos*scaledResolution;
    return -((pos-floor(pos))-vec2(0.5));
}

// 1D Gaussian.
float Gaus(float pos, float scale){
    return exp2(scale*pos*pos);
}

// Return scanline weight.
float Scan(vec2 pos){
    float dst=Dist(pos).y;
    return Gaus(dst, hardScan);
}

// Allow nearest three lines to effect pixel.
vec4 Tri(vec2 pos){
    return Fetch(pos) * Scan(pos);
}

// Distortion of scanlines, and end of screen alpha.
vec2 Warp(vec2 pos){
    pos=pos*2.0-1.0;
    return pos * ((pos.yx * pos.yx * warp) + 1.0) * 0.5 + 0.5;
    //pos*=vec2(1.0+(pos.y*pos.y)*warp.x, 1.0+(pos.x*pos.x)*warp.y);
    //return pos*0.5+0.5;
}

// Shadow mask.
vec3 Mask(vec2 pos){
    pos.x+=pos.y*3.0;
    vec3 mask=vec3(maskDark, maskDark, maskDark);
    pos.x=fract(pos.x/6.0);
    if (pos.x<0.333)mask.r=maskLight;
    else if (pos.x<0.666)mask.g=maskLight;
    else mask.b=maskLight;
    return mask;
}

// Entry.
void main(){
    vec2 pos=Warp(FragCoord);

    vec4 colorSample = Fetch(pos);

    FragColor=vec4(colorSample.rgb * Scan(pos) * Mask(FragCoord * scaledPixelResolution), colorSample.a);

    //fragColor = texture(sampler, pos);
    FragColor.rgb=(FragColor.rgb);
}