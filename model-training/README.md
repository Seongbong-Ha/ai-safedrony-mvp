conda deactivate로 현재 가상환경 종료

콘다 가상환경 설정(python 3.8)
conda create -n safedrony python=3.8

가상환경 활성화
conda activate safedrony


기본 라이브러리 설치
pip install torch torchvision
pip install tensorflow==2.9.1
pip install onnx onnx-tf opencv-python numpy

YOLOv5 모델 로드

