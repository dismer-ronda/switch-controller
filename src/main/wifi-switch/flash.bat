python <esp path>/esp-idf/components/esptool_py/esptool/esptool.py --chip esp32 --port <Puerto> --baud 115200 --before default_reset --after hard_reset write_flash -z --flash_mode dio --flash_freq 40m --flash_size detect 0x1000 c:\wifi-switch\bootloader.bin 0x10000 c:\wifi-switch\wifi-switch.bin 0x8000 c:\wifi-switch\partitions_singleapp.bin

